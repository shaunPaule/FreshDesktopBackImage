package com.bitfly.image.tip;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;

/**
 * 参考 https://blog.csdn.net/qq_36828207/article/details/78784461
 */
public class EditDesktop {

    public static void main(String[] args) {
        change("C:\\Users\\shaun\\Pictures\\img2.jpg");
    }

    private interface MyUser32 extends StdCallLibrary {
        MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class);
        boolean SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }


    public static  void change(String img){

        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", img);
        //WallpaperStyle = 10 (Fill), 6 (Fit), 2 (Stretch), 0 (Tile), 0 (Center)
        //For windows XP, change to 0
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "10"); //fill
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");   // no tiling

        // refresh the desktop using User32.SystemParametersInfo(), so avoiding an OS reboot
        int SPI_SETDESKWALLPAPER = 0x14;
        int SPIF_UPDATEINIFILE = 0x01;
        int SPIF_SENDWININICHANGE = 0x02;

        // User32.System
        boolean result = MyUser32.INSTANCE.SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0,
                img, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE );
    }

}