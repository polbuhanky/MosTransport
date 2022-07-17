package dev.artem.mostransport.custom;

import android.app.Dialog;
import android.util.Log;
import android.widget.LinearLayout;

import dev.artem.mostransport.R;

public class ProgressBarCustom {
    public static final int MAXLEN = 10;

    public Dialog dialog;

    public LinearLayout[] linersIznos;
    public LinearLayout[] linersVibr;
    public LinearLayout[] linersSred;
    public LinearLayout[] linersKoleya;

    public int percentIznos;
    public int percentVibr;
    public int percentSred;
    public int percentkoleya;

    public ProgressBarCustom(Dialog dialog){
        this.dialog = dialog;

        LinearLayout[] linersIznos = {
                (LinearLayout)dialog.findViewById(R.id.Iznos1),
                (LinearLayout)dialog.findViewById(R.id.Iznos2),
                (LinearLayout)dialog.findViewById(R.id.Iznos3),
                (LinearLayout)dialog.findViewById(R.id.Iznos4),
                (LinearLayout)dialog.findViewById(R.id.Iznos5),
                (LinearLayout)dialog.findViewById(R.id.Iznos6),
                (LinearLayout)dialog.findViewById(R.id.Iznos7),
                (LinearLayout)dialog.findViewById(R.id.Iznos8),
                (LinearLayout)dialog.findViewById(R.id.Iznos9),
                (LinearLayout)dialog.findViewById(R.id.Iznos10)
        };

        LinearLayout[] linersVibr = {
                (LinearLayout)dialog.findViewById(R.id.Vibr1),
                (LinearLayout)dialog.findViewById(R.id.Vibr2),
                (LinearLayout)dialog.findViewById(R.id.Vibr3),
                (LinearLayout)dialog.findViewById(R.id.Vibr4),
                (LinearLayout)dialog.findViewById(R.id.Vibr5),
                (LinearLayout)dialog.findViewById(R.id.Vibr6),
                (LinearLayout)dialog.findViewById(R.id.Vibr7),
                (LinearLayout)dialog.findViewById(R.id.Vibr8),
                (LinearLayout)dialog.findViewById(R.id.Vibr9),
                (LinearLayout)dialog.findViewById(R.id.Vibr10)
        };

        LinearLayout[] linersSred = {
                (LinearLayout)dialog.findViewById(R.id.Sred1),
                (LinearLayout)dialog.findViewById(R.id.Sred2),
                (LinearLayout)dialog.findViewById(R.id.Sred3),
                (LinearLayout)dialog.findViewById(R.id.Sred4),
                (LinearLayout)dialog.findViewById(R.id.Sred5),
                (LinearLayout)dialog.findViewById(R.id.Sred6),
                (LinearLayout)dialog.findViewById(R.id.Sred7),
                (LinearLayout)dialog.findViewById(R.id.Sred8),
                (LinearLayout)dialog.findViewById(R.id.Sred9),
                (LinearLayout)dialog.findViewById(R.id.Sred10)
        };

        LinearLayout[] linersKoleya = {
                (LinearLayout)dialog.findViewById(R.id.Koleya1),
                (LinearLayout)dialog.findViewById(R.id.Koleya2),
                (LinearLayout)dialog.findViewById(R.id.Koleya3),
                (LinearLayout)dialog.findViewById(R.id.Koleya4),
                (LinearLayout)dialog.findViewById(R.id.Koleya5),
                (LinearLayout)dialog.findViewById(R.id.Koleya6),
                (LinearLayout)dialog.findViewById(R.id.Koleya7),
                (LinearLayout)dialog.findViewById(R.id.Koleya8),
                (LinearLayout)dialog.findViewById(R.id.Koleya9),
                (LinearLayout)dialog.findViewById(R.id.Koleya10)
        };

        this.linersIznos = linersIznos;
        this.linersVibr = linersVibr;
        this.linersSred = linersSred;
        this.linersKoleya = linersKoleya;
    }

    public void SetPrecent(String name, int percent){
        int fill = (int) Math.ceil(((double)percent / (double)MAXLEN));

        LinearLayout[] layout = null;

        int resid = R.drawable.stroke_green;

        switch (name.toLowerCase()){
            case "iznos":
                layout = this.linersIznos;
                this.percentIznos = percent;
                break;
            case "vibr":
                layout = this.linersVibr;
                this.percentVibr = percent;
                resid = (R.drawable.stroke_yellow);
                break;
            case "sred":
                layout = this.linersSred;
                this.percentSred = percent;
                break;
            case "koleya":
                layout = this.linersKoleya;
                this.percentkoleya = percent;
                break;
            default:
                if (layout == null) {
                    Log.d("SetPrecent: ", "name: " + name + " is not defind");
                    return;
                }
        }

        for (int i = 0; i < fill; i++) {
            layout[i].setBackgroundResource(resid);
        }
    }

}
