package poi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Created by admin on 07.04.2017.
 */
public class poem {
    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousChannelGroup group =AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(10));
        AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open(group);
        assc.bind(new InetSocketAddress(80));
        assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel c, Void param) {
                assc.accept(null,this);
                process(c);
            }



            @Override
            public void failed(Throwable exc, Void param) {
                exc.printStackTrace();
            }
        });


        Object x = new Object();
        synchronized (x){
            x.wait();
        }
    }

    private static void process(AsynchronousSocketChannel c) {
        ByteBuffer buf= ByteBuffer.allocate(10240);
        StringBuilder reguest = new StringBuilder();
        c.read(buf,null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                buf.flip();
                byte[] data = new  byte[buf.remaining()];
                buf.get(data);
                reguest.append(new String(data, StandardCharsets.US_ASCII));
                int len= reguest.length();
                if (len>=4&& reguest.substring(len-4).equals("\r\n\r\n")) {
                    System.out.println(reguest);
                    SendResponse(c);
                }
                else {
                    buf.clear();
                    c.read(buf,null,this);

                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {



    }


        });
    }

    private static void SendResponse(AsynchronousSocketChannel c)  {
        ByteBuffer buff = ByteBuffer.wrap("AntiSocialSocialClub".getBytes());
        c.write(buff, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                try{
                    c.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });
    }

}
