import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import messaging.MsgEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {

    public static void main(String[] args) throws Exception {


        MeterRegistry registry = new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);

        Timer timer = registry.timer("message rate");

        Logger.getLogger("io.netty").setLevel(Level.OFF);

        Thread serverThread = new Thread(){
            public void run(){
                try {
                    System.out.println("Starting Server");
                    ObjectEchoServer es = new ObjectEchoServer();
                    es.runServer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        serverThread.start();

        int i = 0;
        while (i < 1) {
            new Thread() {
                public void run() {
                    try {
                       ObjectEchoClient ec = new ObjectEchoClient();
                        //Thread.sleep((long)(Math.random() * 100));
                        //public MsgEvent(Type msgType, String src_region, String src_agent, String src_plugin, String dst_region, String dst_agent, String dst_plugin, boolean isRegional, boolean isGlobal) {
                        //

                        ec.runClient(timer, new MsgEvent(MsgEvent.Type.CONFIG, "src_agent","src_agent",null,"dst_region","dst_agent",null,true,true));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
            Thread.sleep(1000);
        i++;
        }

    }

}
