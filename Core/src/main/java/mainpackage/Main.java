package mainpackage;

import com.google.gson.Gson;
import domain.WebserverDbEntity;
import sourcepackage.DBConnection;
import spi.Greeting;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        System.out.println("Händer det inget?");
        ServiceLoader<Greeting> greetings = ServiceLoader.load(Greeting.class);


        for(Greeting g : greetings) {
            System.out.println(g.greeting("Ola"));
        }


        ExecutorService executorService = Executors.newCachedThreadPool();

        try(ServerSocket serverSocket = new ServerSocket(80)) {

            while(true) {
                Socket client = serverSocket.accept();
                executorService.submit(() -> handleConnection(client));
            }

        }
        catch(IOException e) {

            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket client) {

        try {

            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String url = readRequest(inputFromClient);

            OutputStream outputToClient = client.getOutputStream();

            if(url.equals("/cat.png"))
                sendImageResponse(outputToClient);
            else if(url.equals("/dog.jpg"))
                sendDogImageResponse(outputToClient);
            else
                sendJsonResponse(outputToClient);

            inputFromClient.close();
            outputToClient.close();
            client.close();

        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendDogImageResponse(OutputStream outputToClient) throws IOException {

        String header = "";
        byte[] data = new byte[0];

        File f = Path.of("Core", "target", "web", "dog.jpg").toFile();
        if(!f.exists() && !f.isDirectory()) {
            header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        } else {

            try(FileInputStream fileInputStream = new FileInputStream(f)) {

                data = new byte[(int)f.length()];
                fileInputStream.read(data);
                String contentType = Files.probeContentType(f.toPath());
                header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-length: " + data.length + "\r\n\r\n";
            }
            catch(IOException e) {
                e.printStackTrace();
            }

        }
        outputToClient.write(header.getBytes());
        outputToClient.write(data);
        outputToClient.flush();
    }


    private static void sendImageResponse(OutputStream outputToClient) throws IOException {

        String header  ="";
        byte[] data = new byte[0];

        File f = Path.of("Core", "target", "web", "cat.png").toFile();
        if(!f.exists() && !f.isDirectory()) {
            header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        } else {

            try(FileInputStream fileInputStream = new FileInputStream(f)) {

                data = new byte[(int)f.length()];
                fileInputStream.read(data);
                String contentType = Files.probeContentType(f.toPath());
                header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-length: " + data.length + "\r\n\r\n";
            }
            catch(IOException e) {

                e.printStackTrace();
            }
        }
        outputToClient.write(header.getBytes());
        outputToClient.write(data);
        outputToClient.flush();

    }

    private static void sendJsonResponse(OutputStream outputToClient) throws IOException {


        List<WebserverDbEntity> persons = DBConnection.getPeopleFromDb();

        Gson gson = new Gson();
        String json = gson.toJson(persons);
        System.out.println(json);

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-length: " + data.length + "\r\n\r\n";

        outputToClient.write(header.getBytes());
        outputToClient.write(data);
        outputToClient.flush();

    }

    private static String readRequest(BufferedReader inputFromClient) throws IOException {

        String url = "";

        while(true) {

            String line = inputFromClient.readLine();
            if(line.startsWith("GET"))
                url = line.split(" ")[1];
            if(line == null || line.isEmpty())
                break;
        }
        return url;
    }
}
