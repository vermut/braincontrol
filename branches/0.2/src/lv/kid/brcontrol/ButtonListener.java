package lv.kid.brcontrol;

public interface ButtonListener {
    void buttonPressed(int button);
    void buttonReleased(int button);
    void buttonQueued(int button);
}
