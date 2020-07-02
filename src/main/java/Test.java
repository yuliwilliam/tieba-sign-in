public class Test {
    public static void main(String[] args){

        try {
            System.out.println(new CaptchaSolver().solveCaptcha("./genimg.gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
