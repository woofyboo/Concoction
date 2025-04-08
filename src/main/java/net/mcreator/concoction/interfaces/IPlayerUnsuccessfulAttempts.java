package net.mcreator.concoction.interfaces;

public interface IPlayerUnsuccessfulAttempts {
    int concoction$getUnsuccessfulAttempts();

    void concoction$setUnsuccessfulAttempts(int unsuccessfulAttempts);
    void concoction$incrementUnsuccessfulAttempts();
    void concoction$decrementUnsuccessfulAttempts();
}
