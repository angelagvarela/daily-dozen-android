package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.joanzapata.iconify.widget.IconTextView;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Weights;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import timber.log.Timber;

public class DateWeights extends LinearLayout {
    @BindView(R.id.header)
    protected TextView tvHeader;
    @BindView(R.id.eye)
    protected TextView tvEye;
    @BindView(R.id.morning_weight)
    protected TextView tvMorningWeight;
    @BindView(R.id.morning_weight_hidden_icon)
    protected IconTextView tvMorningWeightHiddenIcon;
    @BindView(R.id.evening_weight)
    protected TextView tvEveningWeight;
    @BindView(R.id.evening_weight_hidden_icon)
    protected IconTextView tvEveningWeightHiddenIcon;

    private boolean initialized = false;
    private boolean showWeights = true;
    private Day day;

    public DateWeights(Context context) {
        super(context);
        init(context);
    }

    public DateWeights(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        final View view = inflate(context, R.layout.date_weights, this);
        ButterKnife.bind(this, view);

        tvHeader.setText("Weight");
    }

    public void setDay(final Day day) {
        this.day = day;

        final Weights weightsOnDay = Weights.getWeightsOnDay(day);
        if (weightsOnDay != null) {
            if (weightsOnDay.getMorningWeight() > 0) {
                setMorningWeight(weightsOnDay.getMorningWeight());
            }
            if (weightsOnDay.getEveningWeight() > 0) {
                setEveningWeight(weightsOnDay.getEveningWeight());
            }
        }

        initialized = true;
    }

    @OnClick(R.id.eye)
    public void onEyeClicked() {
        showWeights = !showWeights;

        if (showWeights) {
            setWeightsVisible();
        } else {
            setWeightsInvisible();
        }
    }

    private void setWeightsVisible() {
        tvEye.setText(R.string.date_weights_eye_open);
        tvMorningWeight.setVisibility(VISIBLE);
        tvMorningWeightHiddenIcon.setVisibility(GONE);
        tvEveningWeight.setVisibility(VISIBLE);
        tvEveningWeightHiddenIcon.setVisibility(GONE);
    }

    private void setWeightsInvisible() {
        tvEye.setText(R.string.date_weights_eye_closed);
        tvMorningWeight.setVisibility(GONE);
        tvMorningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(tvMorningWeight.getText())) {
            tvMorningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            tvMorningWeightHiddenIcon.setText(R.string.checked);
        }

        tvEveningWeight.setVisibility(GONE);
        tvEveningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(tvEveningWeight.getText())) {
            tvEveningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            tvEveningWeightHiddenIcon.setText(R.string.checked);
        }
    }

    @OnTextChanged({R.id.morning_weight, R.id.evening_weight})
    public void onWeightChanged() {
        if (!initialized) {
            return;
        }

        try {
            float morningWeight = 0;
            float eveningWeight = 0;

            String morningWeightStr = tvMorningWeight.getText().toString();
            if (!TextUtils.isEmpty(morningWeightStr)) {
                morningWeight = Float.parseFloat(morningWeightStr);
            }

            String eveningWeightStr = tvEveningWeight.getText().toString();
            if (!TextUtils.isEmpty(eveningWeightStr)) {
                eveningWeight = Float.parseFloat(eveningWeightStr);
            }

            if (morningWeight > 0 || eveningWeight > 0) {
                day = Day.createDayIfDoesNotExist(day);
                Weights.createWeightsIfDoesNotExist(day, morningWeight, eveningWeight);
                Timber.d("Saving morning weight [%s] and evening weight [%s]", morningWeight, eveningWeight);
            }
        } catch (NumberFormatException e) {
            Timber.e(e);
        }
    }

    @OnEditorAction({R.id.morning_weight,R.id.evening_weight})
    public boolean onMorningWeightEditorAction(EditText et, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et.clearFocus();
        }
        return false;
    }

    public void setMorningWeight(final float weight) {
        if (weight > 0) {
            tvMorningWeight.setText(String.valueOf(weight));
        }
    }

    public void setEveningWeight(final float weight) {
        if (weight > 0) {
            tvEveningWeight.setText(String.valueOf(weight));
        }
    }
}
