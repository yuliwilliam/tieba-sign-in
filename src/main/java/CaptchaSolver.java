import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;

public class CaptchaSolver {


    public CaptchaSolver() {
    }

    public String solveCaptcha(String imagePath) throws TesseractException, IOException {


        System.out.println(imagePath);
        File image = new File(imagePath);
        System.out.println(image.getAbsolutePath());
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("./tessdata");
        String text = tesseract.doOCR(image);

        return text;
    }

}
