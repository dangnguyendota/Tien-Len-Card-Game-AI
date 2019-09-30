package com.ndn.base;

import com.ndn.algorithm.GameConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author dangnguyendota
 */
public interface Game extends Serializable {
    /**
     * Thực hiện đánh bài
     * @param object bộ bài cần đánh ra.
     */
    void move(BaseObject object);

    /**
     * Lấy thông tin người chơi đang có lượt
     * @return người chơi
     */
    Player getCurrentPlayer();

    int getCurrentPlayerIndex();

    int getPreviousPlayerIndex();

    int getWinner();

    /**
     * Lấy số lượng người chơi trong bàn
     * @return số người đang choi
     */
    int getMaxPlayer();

    /**
     * lấy bản sao của <Class>Game</Class>
     * khi thay đổi các thông số trong bản sao thì bản gốc ko bị thay đổi
     * @return bản sao
     */
    Game getCopy();

    /**
     * Lấy nước đi có thể đi bây giờ của người chơi hiện tại
     * @return
     */
    ArrayList<BaseObject> getAvailableMoves();

    /**
     * kiểm tra xem trò chơi đã kết thúc chưa
     * @return kết quả là true nếu trò chơi đã kết thúc và ngược lại
     */
    boolean end();

    /**
     * Chơi linh tinh cho đến khi end game
     */
    void playRandomly();

    /**
     * chỉ gọi được khi trò chơi kết thúc.
     * @return kết quả của trận đấu
     */
    Reward getReward();

    /**
     * cho người chơi vào bàn
     * @param player người chơi
     */
    void put(Player player);

    /**
     * @return số lượng người chơi hiện có
     */
    int size();

    /**
     * scan trước trân đấu để index một số dữ liệu dùng trong mcts
     * lưu ý scan chỉ dùng 1 lần sau khi đã add hết player vào game.
     */
    void scan();

    /**
     * Chuyển lượt chơi cho người kế tiếp.
     */
    void next();

    GameConfiguration getConfig();

    void setConfig(GameConfiguration config);

    Player getPlayer(int index);
}

