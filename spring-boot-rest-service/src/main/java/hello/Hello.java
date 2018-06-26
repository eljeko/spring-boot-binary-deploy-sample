package hello;

public class Hello {

    private final String message;
    private final String externalmessage;

    public Hello(String message, String external_message) {
        this.message = message;
        this.externalmessage = external_message;
    }

    public String getMessage() {
        return message;
    }

    public String getExternalmessage() {
        return externalmessage;
    }
}
