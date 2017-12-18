// IPlayerManager.aidl
package zte.com.ipc;

import zte.com.ipc.Music;

interface IPlayerManager {
    void updatePlayList(in List<Music> list);

    void actionPlay(in int idx);
    void actionStart();
    void actionPause();
    void actionPrevious();
    void actionNext();

    int actionGetProgress();
    void actionSeekTo(in int progress);
}
