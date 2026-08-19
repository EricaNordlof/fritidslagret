package se.chasacademy.fritidslagret.ui;

/** Signalerar att konsolens inmatningsström har stängts, till exempel med Ctrl+D. */
public final class InputClosedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InputClosedException() {
        super("Konsolinmatningen har avslutats.");
    }
}
