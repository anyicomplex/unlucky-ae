package com.anyicomplex.unlucky.ui.battleui;

import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.event.Battle;
import com.anyicomplex.unlucky.map.TileMap;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.screen.GameScreen;
import com.anyicomplex.unlucky.ui.UI;

/**
 * Superclass for all UI related to battle events
 *
 * @author Ming Li
 */
public abstract class BattleUI extends UI {

    protected Battle battle;
    protected BattleUIHandler uiHandler;

    public BattleUI(GameScreen gameScreen, TileMap tileMap, Player player, Battle battle,
                    BattleUIHandler uiHandler, ResourceManager rm) {
        super(gameScreen, tileMap, player, rm);
        this.battle = battle;
        this.uiHandler = uiHandler;
    }

}
