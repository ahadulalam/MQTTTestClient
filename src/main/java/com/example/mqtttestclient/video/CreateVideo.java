package com.example.mqtttestclient.video;

import com.example.mqtttestclient.image.CreateImage;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import static com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Service
public class CreateVideo {
    long nextFrameTime = 0;
    public Queue<String> photoQueue = new LinkedList<>();
    public Queue<Byte[]> photoQueueByteArray = new LinkedList<>();
    // video parameters

    int videoStreamIndex = 0;
    final int videoStreamId = 0;
    final long frameRate = DEFAULT_TIME_UNIT.convert(250, MILLISECONDS);
    final int width = 320;
    final int height = 200;
    final IMediaWriter writer = ToolFactory.makeWriter("test.avi");

    final int a = writer.addVideoStream(videoStreamIndex, videoStreamId, width, height);


    public void createVideo(){

            String imageName = photoQueue.remove();
            System.out.println("Video Data");
            System.out.println("------------------------------");
            System.out.println("File Name: " + imageName);
            System.out.println("Video Stream Index: "+ videoStreamIndex);
            System.out.println("------------------------------");

            try {
                /*writer.addListener(ToolFactory.makeViewer(
                        IMediaViewer.Mode.VIDEO_ONLY, true,
                        javax.swing.WindowConstants.EXIT_ON_CLOSE));*/


                //writer.addAudioStream(audioStreamIndex, audioStreamId, channelCount, sampleRate);

                File file = new File(imageName);
                BufferedImage frame = ImageIO.read(file);
                writer.encodeVideo(videoStreamIndex, frame, nextFrameTime, DEFAULT_TIME_UNIT);
                nextFrameTime += frameRate;

                if (photoQueue.isEmpty()) {
                    writer.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void createVideo1(byte[] data){
        /*String imageName = photoQueue.remove();
        System.out.println("Video Data");
        System.out.println("------------------------------");
        System.out.println("File Name: " + imageName);
        System.out.println("Video Stream Index: "+ videoStreamIndex);
        System.out.println("------------------------------");*/

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage frame = ImageIO.read(bis);

            //File file = new File(imageName);
            //BufferedImage frame = ImageIO.read(file);
            writer.encodeVideo(videoStreamIndex, frame, nextFrameTime, DEFAULT_TIME_UNIT);
            nextFrameTime += frameRate;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
