package Interfaces;

public interface GameController {
    void startGame();
    void accionar(int rol, int keycode);
    void heroDied(int heroVida, int intentosRestantes, float multVida, float multDanio, float multVelocidad, float multSalto);
    void heroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto);
    void resumeGame();
	void setPlayerRole(int assignedRole);
}