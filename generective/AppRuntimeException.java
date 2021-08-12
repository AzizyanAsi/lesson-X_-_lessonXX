package generective;

public class AppRuntimeException extends RuntimeException {
    @Override
    public String toString() {
        return "file not readable";
    }

}
