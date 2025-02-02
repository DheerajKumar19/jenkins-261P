package hudson.cli;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

public class CLITest {

    @Test
    public void testPlainHttpConnection() throws IOException, InterruptedException {
        // Create mock dependencies
        CLIConnectionFactory mockFactory = mock(CLIConnectionFactory.class);
        CLI.ClientSideImpl mockConnection = mock(CLI.ClientSideImpl.class);
        FullDuplexHttpStream mockStreams = mock(FullDuplexHttpStream.class);
        InputStream mockInputStream = mock(InputStream.class);

        // Configure mock objects
        when(mockFactory.authorization("nppatil")).thenReturn(mock(CLIConnectionFactory.class));
        when(mockStreams.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(mockStreams.getInputStream()).thenReturn(mockInputStream);
        when(mockConnection.exit()).thenReturn(0);
        when(mockInputStream.read()).thenReturn(0);

        // Create test object
        HttpConnectionManager connectionManager = new HttpConnectionManager(mockFactory);

        // Call method under test
        int result = connectionManager.plainHttpConnection("http://localhost:8888/", Arrays.asList("help"));

//        // Verify results
        assertEquals(0, result);
        verify(mockFactory).authorization("nppatil");
        verify(mockStreams).getOutputStream();
        verify(mockStreams).getInputStream();
        verify(mockConnection).start(Arrays.asList("arg1", "arg2"));
        verify(mockConnection).sendEncoding(anyString());
        verify(mockConnection).exit();
        verify(mockInputStream).read();
    }
}
