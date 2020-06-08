package program.model;

import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class GridPicCollection {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private HashMap<String, Image> picCollection = new HashMap<>();
    private Image outImage;
    private int rows;
    private int columns;
    private Image Question = new Image("question.png");
    private Image Plus = new Image("plus.png");

    public GridPicCollection(int Column, int Row) {
        rows = Row;
        columns = Column;
        for (int row = 0; row < rows; row++)
            for (int column = 0; column < columns; column++) {
                if ((row == 0) && (column == 0))
                    picCollection.put("Image_" + column + "_" + row, Plus);
                else
                    picCollection.put("Image_" + column + "_" + row, null);
            }
        outImage = Question;
    }

    public Image findPic(String key) {
        for (Map.Entry entry : picCollection.entrySet()) {
            if (entry.getKey().equals(key))
                return (Image) entry.getValue();
        }
        return null;
    }

    public Image getOutPic() {
        return outImage;
    }

    public void addPic(String key, Image image) {
        picCollection.put(key, image);
    }

    public void addPlus(String key) {
        picCollection.put(key, Plus);
    }

    public void clear() {
        for (int row = 0; row < rows; row++)
            for (int column = 0; column < columns; column++) {
                if ((row == 0) && (column == 0))
                    picCollection.put("Image_" + column + "_" + row, Plus);
                else
                    picCollection.put("Image_" + column + "_" + row, null);
            }
        outImage = Question;
    }

    public String getNextId(String id) {
        String ans;
        int column = Character.getNumericValue(id.charAt(6));
        int row = Character.getNumericValue(id.charAt(8));
        if ((column == (this.columns - 1)) && (row == (this.rows - 1)))
            return null;
        if (column == 2) {
            column = 0;
            ans = "Image_" + column + "_" + ++row;
        } else
            ans = "Image_" + ++column + "_" + row;
        return ans;
    }

    public String getPlusPicId() {
        for (Map.Entry entry : picCollection.entrySet()) {
            if (entry.getValue() == Plus)
                return (String) entry.getKey();
        }
        return "Image_0_6"; //Количество картинок для модели
    }

    public int numOfImages() {
        int size = 0;
        for (Map.Entry entry : picCollection.entrySet()) {
            if ((Image) entry.getValue() != null)
                size++;
        }
        return size - 1;
    }

    public boolean sizeOfImages() {
        double width = 0;
        double height = 0;
        for (Map.Entry entry : picCollection.entrySet()) {
            Image currentImage = (Image) entry.getValue();
            if ((currentImage != null) && (currentImage != Plus)) {
                if (width == 0) {
                    width = currentImage.getWidth();
                    height = currentImage.getHeight();
                } else {
                    if (width != currentImage.getWidth())
                        return false;
                    if (height != currentImage.getHeight())
                        return false;
                }
            }
        }
        return true;
    }

    public void shift(String id) {
        boolean end = true;
        while (!id.equals("Image_0_6")) { //Количество картинок для модели
            if (id.equals("Image_2_5") && (findPic(id) != null) && (findPic(id) != Plus)) { //Количество картинок для модели
                addPlus(id);
                return;
            }
            String newId = getNextId(id);
            addPic(id, findPic(newId));
            if (findPic(newId) == Plus) {
                addPic(newId, null);
                return;
            }
            id = newId;
        }
    }


    public boolean sendPictures() throws IOException {

        String url = "http://example.com//smth";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
        StringBuilder data = new StringBuilder();
        data.append("{\n");
        for (Map.Entry entry : picCollection.entrySet()) {
            if ((entry.getValue() != null) && (entry.getValue() != Plus)) {
                Mat img = Imgcodecs.imread(((Image) entry.getValue()).getUrl().substring(5));
                //System.out.println(((Image)entry.getValue()).getUrl().substring(5));
                MatOfByte img_byte = new MatOfByte();
                Imgcodecs.imencode(".jpg", img, img_byte);
                System.out.println(img_byte.toString());
                data.append("image: ");
                data.append(img_byte.toString());
                data.append(",\n");
                //Imgcodecs.imwrite("C:\\Users\\User\\Desktop\\OUT\\"+ (String) entry.getKey()+".jpg",img);
            }
        }
        data.append("}");

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data.toString());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        //TODO: обработка ответа

        /*byte[] byteOut = data.toString().getBytes(StandardCharsets.UTF_8);
        int length = byteOut.length;
        http.setFixedLengthStreamingMode(length);
        try (OutputStream os = http.getOutputStream()) {
            os.write(byteOut);
        }
        InputStream is = http.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            result.write(buffer, 0, len);
        }
        String ans = result.toString(StandardCharsets.UTF_8.name());*/

        //System.out.println(data.toString());
        //outImage=new Image("file:C:\\Users\\User\\Desktop\\out.jpg");*/
        return (outImage != Question);
    }
}
