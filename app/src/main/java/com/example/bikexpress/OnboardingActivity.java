package com.example.bikexpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private View[] indicators;
    private int currentPage = 0;

    private final String[] titles = {
            "Find Your Perfect Ride",
            "Book in Seconds",
            "Ride. Track. Enjoy."
    };
    private final String[] subtitles = {
            "Choose from mountain, city, electric and more bikes for every adventure",
            "Browse available bikes, pick your duration and confirm with one tap",
            "Real-time ride tracking, live timer and easy payment in one app"
    };
    private final String[] emojis = {"🚵", "📱", "🗺"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.onboarding_viewpager);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        indicators = new View[]{
                findViewById(R.id.indicator_0),
                findViewById(R.id.indicator_1),
                findViewById(R.id.indicator_2)
        };

        viewPager.setAdapter(new OnboardingAdapter());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateIndicators(position);
                btnNext.setText(position == 2 ? "Get Started" : "Next");
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < 2) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit().putBoolean("hasSeenOnboarding", true).apply();
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void updateIndicators(int active) {
        for (int i = 0; i < indicators.length; i++) {
            ViewGroup.LayoutParams lp = indicators[i].getLayoutParams();
            if (i == active) {
                lp.width = dpToPx(24);
                indicators[i].setBackgroundResource(R.drawable.bg_onboarding_indicator_active);
            } else {
                lp.width = dpToPx(8);
                indicators[i].setBackgroundResource(R.drawable.bg_onboarding_indicator_inactive);
            }
            indicators[i].setLayoutParams(lp);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_onboarding_page, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.title.setText(titles[position]);
            holder.subtitle.setText(subtitles[position]);
            holder.emojiView.setText(emojis[position]);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView title, subtitle, emojiView;

            VH(View v) {
                super(v);
                title = v.findViewById(R.id.onboarding_title);
                subtitle = v.findViewById(R.id.onboarding_subtitle);
                emojiView = v.findViewById(R.id.onboarding_image);
            }
        }
    }
}