package zte.com.jellypull;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.img_view);
        iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ObjectAnimator oa = null;
        Random r = new Random();
        switch (r.nextInt(6))
        {
            case 1:
                oa = ObjectAnimator.ofFloat(v, "TranslationX", 300, 100).setDuration(500);
                break;
            case 2:
                oa = ObjectAnimator.ofFloat(v, "TranslationY", 200, 300).setDuration(500);
                break;
            case 3:
                oa = ObjectAnimator.ofFloat(v, "scaleX", 0.0f, 1.5f, 1.0f).setDuration(500);
                break;
            case 4:
                oa = ObjectAnimator.ofFloat(v, "rotation", 0, 270).setDuration(500);
                break;
            case 5:
                oa = ObjectAnimator.ofFloat(v,"alpha",1,0,1).setDuration(500);
                break;
            case 0:
                AnimatorSet as = new AnimatorSet();
                as.playTogether(ObjectAnimator.ofFloat(v, "TranslationX", 0).setDuration(500));
                as.playTogether(ObjectAnimator.ofFloat(v, "TranslationY", 0).setDuration(500));
                as.playTogether(ObjectAnimator.ofFloat(v, "scaleX", 1.0f).setDuration(500));
                as.playTogether(ObjectAnimator.ofFloat(v, "rotation", 0).setDuration(500));

                as.start();
            default:
                break;
        }

        if(oa != null)
        {
            oa.start();
        }
    }
}
