
package jdiskmark;

import static jdiskmark.App.KILOBYTE;
import static jdiskmark.App.MEGABYTE;
import static jdiskmark.App.blockSizeKb;
import static jdiskmark.App.msg;
import static jdiskmark.App.numOfBlocks;
import static jdiskmark.App.testFile;
import static jdiskmark.App.dataDir;
import static jdiskmark.App.numOfMarks;
import static jdiskmark.DiskMark.MarkType.READ;
import static jdiskmark.DiskMark.MarkType.WRITE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Thread running the disk benchmarking. only one of these threads can run at
 * once.
 */
public class DiskWorker extends SwingWorker <Boolean, DiskMark> {
    
    @Override
    protected Boolean doInBackground() throws Exception {
        
        System.out.println("*** starting new worker thread");
        msg("Running readTest "+App.readTest+"   writeTest "+App.writeTest);
        msg("num files: "+App.numOfMarks+", num blks: "+App.numOfBlocks
           +", blk size (kb): "+App.blockSizeKb+", blockSequence: "+App.blockSequence);
        
        int wUnitsComplete = 0,
            rUnitsComplete = 0,
            unitsComplete;
        
        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;
        
        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }
   
        DiskMark wMark, rMark;
        
        Gui.updateLegend();
        
        if (App.autoReset == true) {
            App.resetTestData();
            Gui.resetTestData();
        }
        
        int startFileNum = App.nextMarkNumber;
        
        if(App.writeTest) {
            DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
            run.numMarks = App.numOfMarks;
            run.numBlocks = App.numOfBlocks;
            run.blockSize = App.blockSizeKb;
            run.txSize = App.targetTxSizeKb();
            
            if (App.multiFile == false) {
                testFile = new File(dataDir.getAbsolutePath()+File.separator+"testdata.jdm");
            }            
            for (int m=startFileNum; m<startFileNum+App.numOfMarks && !isCancelled(); m++) {
                
                if (App.multiFile == true) {
                    testFile = new File(dataDir.getAbsolutePath()
                            + File.separator+"testdata"+m+".jdm");
                }   
                wMark = new DiskMark(WRITE);
                wMark.markNum = m;
                long startTime = System.nanoTime();
                long totalBytesWrittenInMark = 0;

                String mode = "rw";
                if (App.writeSyncEnable) { mode = "rwd"; }
                
                try {
                    try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,mode)) {
                        for (int b=0; b<numOfBlocks; b++) {
                            if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                                int rLoc = Util.randInt(0, numOfBlocks-1);
                                rAccFile.seek(rLoc*blockSize);
                            } else {
                                rAccFile.seek(b*blockSize);
                            }
                            rAccFile.write(blockArr, 0, blockSize);
                            totalBytesWrittenInMark += blockSize;
                            wUnitsComplete++;
                            unitsComplete = rUnitsComplete + wUnitsComplete;
                            percentComplete = (float)unitsComplete/(float)unitsTotal * 100f;
                            setProgress((int)percentComplete);
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
                long endTime = System.nanoTime();
                long elapsedTimeNs = endTime - startTime;
                double sec = (double)elapsedTimeNs / (double)1000000000;
                double mbWritten = (double)totalBytesWrittenInMark / (double)MEGABYTE;
                wMark.bwMbSec = mbWritten / sec;
                msg("m:"+m+" write IO is "+wMark.getBwMbSec()+" MB/s     "
                        + "("+Util.displayString(mbWritten)+ "MB written in "
                        + Util.displayString(sec)+" sec)");
                App.updateMetrics(wMark);
                publish(wMark);
                
                run.runMax = wMark.cumMax;
                run.runMin = wMark.cumMin;
                run.runAvg = wMark.cumAvg;
                run.endTime = new Date();
            }
            
            run.setDataPath(dataDir.getAbsolutePath());
            Path dataDirPath = Paths.get(dataDir.getAbsolutePath());
            run.setDriveType(Util.getDriveType(dataDirPath.getRoot().toFile()));
            
            EntityManager em = EM.getEntityManager();
            em.getTransaction().begin();
            em.persist(run);
            em.getTransaction().commit();
            
            Gui.runPanel.addRun(run);
        }
        
        
        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !isCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame, 
                "For valid READ measurements please clear the disk cache by\n" +
                "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                "Removable drives can be disconnected and reconnected.\n" +
                "For system drives use the WRITE and READ operations \n" +
                "independantly by doing a cold reboot after the WRITE",
                "Clear Disk Cache Now",JOptionPane.PLAIN_MESSAGE);
        }
        
        if (App.readTest) {
            DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
            run.numMarks = App.numOfMarks;
            run.numBlocks = App.numOfBlocks;
            run.blockSize = App.blockSizeKb;
            run.txSize = App.targetTxSizeKb();
            for (int m=startFileNum; m<startFileNum+App.numOfMarks && !isCancelled(); m++) {
                
                if (App.multiFile == true) {
                    testFile = new File(dataDir.getAbsolutePath()
                            + File.separator+"testdata"+m+".jdm");
                }
                rMark = new DiskMark(READ);
                rMark.markNum = m;
                long startTime = System.nanoTime();
                long totalBytesReadInMark = 0;

                try {
                    try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,"r")) {
                        for (int b=0; b<numOfBlocks; b++) {
                            if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                                int rLoc = Util.randInt(0, numOfBlocks-1);
                                rAccFile.seek(rLoc*blockSize);
                            } else {
                                rAccFile.seek(b*blockSize);
                            }
                            rAccFile.readFully(blockArr, 0, blockSize);
                            totalBytesReadInMark += blockSize;
                            rUnitsComplete++;
                            unitsComplete = rUnitsComplete + wUnitsComplete;
                            percentComplete = (float)unitsComplete/(float)unitsTotal * 100f;
                            setProgress((int)percentComplete);
                        }
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
                long endTime = System.nanoTime();
                long elapsedTimeNs = endTime - startTime;
                double sec = (double)elapsedTimeNs / (double)1000000000;
                double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
                rMark.bwMbSec = mbRead / sec;
                msg("m:"+m+" READ IO is "+rMark.bwMbSec+" MB/s    "
                        + "(MBread "+mbRead+" in "+sec+" sec)");
                App.updateMetrics(rMark);
                publish(rMark);
                
                run.runMax = rMark.cumMax;
                run.runMin = rMark.cumMin;
                run.runAvg = rMark.cumAvg;
                run.endTime = new Date();
            }
            Gui.runPanel.addRun(run);
        }
        App.nextMarkNumber += App.numOfMarks;      
        return true;
    }
    
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((m) -> {
            if (m.type==DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(m);
            } else {
                Gui.addReadMark(m);
            }
        });
    }
    
    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }
}
