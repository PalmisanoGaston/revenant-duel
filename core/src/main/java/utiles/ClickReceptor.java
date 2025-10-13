package utiles;

public interface ClickReceptor {
    boolean touchDown(int x, int y, int pointer, int button);
    boolean touchUp(int x, int y, int pointer, int button);
    boolean touchDragged(int x, int y, int pointer);
}
