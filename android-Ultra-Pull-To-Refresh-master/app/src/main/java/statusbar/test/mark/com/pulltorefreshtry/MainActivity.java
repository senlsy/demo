package statusbar.test.mark.com.pulltorefreshtry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import statusbar.test.mark.com.pulltorefreshtry.header.RentalsSunHeaderView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final PtrFrameLayout frame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);

        // header
        final RentalsSunHeaderView header = new RentalsSunHeaderView(this);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        header.setUp(frame);

        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.setPinContent(false);
        frame.setPullToRefresh(false);
        frame.setKeepHeaderWhenRefresh(true);


//        frame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                frame.autoRefresh(true);
//            }
//        }, 100);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                long delay = 3000;
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, delay);
            }
        });


    }
}
