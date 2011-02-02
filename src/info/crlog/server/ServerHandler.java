package info.crlog.server;

import info.crlog.api.RequestHandler;
import info.crlog.api.Version;
import info.crlog.util.Common;
import info.crlog.util.PropertiesManager;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handler implementation for the echo server.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2121 $, $Date: 2010-02-02 09:38:07 +0900 (Tue, 02 Feb 2010) $
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            ServerHandler.class.getName());
    private final AtomicLong transferredBytes = new AtomicLong();
    private RequestHandler handler;
    private PropertiesManager props = new PropertiesManager("conf/settings");
    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Send back the received message to the remote peer.
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        //get the request data
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        char[] data = new char[buf.readableBytes()];
        int i = 0;
        while (buf.readable()) {
            data[i] = (char) buf.readByte();
            i++;
        }
        //user request handler to process data
        handler = new RequestHandler(String.copyValueOf(data));
        //return the data resposne to client
        e.getChannel().write(ChannelBuffers.wrappedBuffer(
                handler.getResults()));

//        System.out.println(data);
//        System.out.flush();
        //once results returned close connection
        e.getChannel().disconnect();
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        if (Boolean.parseBoolean(props.getProperty("logExceptions"))) {
            logger.log(
                    Level.WARNING,
                    "Unexpected exception from downstream.",
                    e.getCause());
        }
        if (Boolean.parseBoolean(props.getProperty("connectionDebug"))) {
            e.getChannel().write(ChannelBuffers.wrappedBuffer(
                    new Common().pushExceptionToClient(Version.V1,
                    e.getCause()).getBytes()));
        }
        e.getChannel().close();
    }
}
