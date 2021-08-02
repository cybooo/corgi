package cz.wake.corgibot.exceptions;

import java.io.Serial;

public class NoAPIKeyException extends Exception {

    @Serial
    private static final long serialVersionUID = -501369307171514453L;

    public NoAPIKeyException() {
        super();
    }

    public NoAPIKeyException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public NoAPIKeyException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public NoAPIKeyException(String arg0) {
        super(arg0);
    }

    public NoAPIKeyException(Throwable arg0) {
        super(arg0);
    }

}
