package com.example.cloudtable;

import android.util.Log;

import com.example.cloudtable.Activity.MainActivity;
import com.example.cloudtable.Database.generator.Tables;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

/**
 * Directs every client request and returns any results
 *
 * @author tombuzbee
 *
 */
public class RequestHandler implements Runnable
{
    private final Socket socket;
//    private final ServerController controller;
    private LineReader reader;
    private PrintWriter writer;
    private StringBuffer writeBuffer;
    private boolean error = false;
    private String message ,data;
    OutputStream outputStream;

    /**
     * Starts the handler on the given socket
     *
     * @param socket
     */
    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        Log.w("masuk", String.valueOf(socket.getInputStream()));

        List<Tables> tables = MainActivity.getTables();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setTables(tables);
        Gson gson = new Gson();
        data = gson.toJson(apiResponse);
    }

    @Override
    public void run()
    {
        Log.w("request thread","run");
        try
        {
			/*
			 * Instead of writing directly to the socket's output stream I use
			 * an intermediate StringBuffer. This allows us to clear the buffer
			 * in case of an error instead of sending an incomplete response.
			 */
            StringWriter stringWriter = new StringWriter(256);
            writeBuffer = stringWriter.getBuffer();
            writer = new PrintWriter(stringWriter, true);

            reader = new LineReader(new InputStreamReader(socket
                    .getInputStream()));

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = in.readLine();
            Log.d("request",data);

            // Process the request
            processRequest();

            // Copy the buffer to the output stream
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(writeBuffer.toString());
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            try
            {
                // Close everything
                writer.close();
//                reader.close();
                socket.close();
            }
            catch (IOException e)
            {
                System.err.println("Error disconnecting from client");
                e.printStackTrace();
            }
        }
    }

    private void processRequest() throws IOException {
        //send feedback to server
        outputStream = socket.getOutputStream();
        PrintStream printStream = new PrintStream(socket
                .getOutputStream());
        printStream.print(data);
        printStream.flush();
        printStream.close();
    }

    public class LineReader
    {
        private BufferedReader reader;
        private String currentLine = null;

        /**
         * Constructs a LineReader around an existing Reader
         *
         * @param reader
         *            The reader to wrap
         */
        public LineReader(Reader reader)
        {
            this.reader = new BufferedReader(reader);
        }

        /**
         * Reads the next line from the stream, discarding the old
         *
         * @return The String just read, or null if the end of the stream has been
         *         reached
         */
        public String advance()
        {
            try
            {
                if (reader != null)
                {
                    currentLine = reader.readLine();
                }
            }
            catch (IOException e)
            {
                currentLine = null;
                e.printStackTrace();
            }
            return currentLine;
        }

        /**
         * @return The most recently read line
         */
        public String getLine()
        {
            return currentLine;
        }

        /**
         * Closes the stored Reader and nulls the stored line
         *
         * @throws IOException
         */
        public void close() throws IOException
        {
            reader.close();
            reader = null;
            currentLine = null;
        }
    }

}
