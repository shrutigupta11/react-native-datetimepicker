/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 * </p>
 */

package com.reactcommunity.rndatetimepicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import javax.annotation.Nullable;

@SuppressWarnings("ValidFragment")
public class RNTimePickerDialogFragment extends DialogFragment {
  private TimePickerDialog instance;

  @Nullable
  private OnTimeSetListener mOnTimeSetListener;
  @Nullable
  private OnDismissListener mOnDismissListener;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Bundle args = getArguments();
    instance = createDialog(args, getActivity(), mOnTimeSetListener);
    return instance;
  }

  public void update(Bundle args) {
    final RNDate date = new RNDate(args);
    instance.updateTime(date.hour(), date.minute());
  }

  static TimePickerDialog createDialog(Bundle args, Context activityContext, @Nullable OnTimeSetListener onTimeSetListener) {
    final RNDate date = new RNDate(args);
    final int hour = date.hour();
    final int minute = date.minute();
    boolean is24hour = DateFormat.is24HourFormat(activityContext);

    int minuteInterval = RNConstants.DEFAULT_TIME_PICKER_INTERVAL;
    if (args != null && RNMinuteIntervals.isValid(args.getInt(RNConstants.ARG_INTERVAL, -1))) {
      minuteInterval = args.getInt(RNConstants.ARG_INTERVAL);
    }

    RNTimePickerDisplay display = RNTimePickerDisplay.DEFAULT;
    if (args != null && args.getString(RNConstants.ARG_DISPLAY, null) != null) {
      if (RNMinuteIntervals.isRadialPickerCompatible(minuteInterval)) {
        display = RNTimePickerDisplay.valueOf(args.getString(RNConstants.ARG_DISPLAY).toUpperCase(Locale.US));
      } else {
        display = RNTimePickerDisplay.SPINNER;
      }
    }

    if (args != null) {
      is24hour = args.getBoolean(RNConstants.ARG_IS24HOUR, DateFormat.is24HourFormat(activityContext));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (display == RNTimePickerDisplay.CLOCK) {
        return new RNDismissableTimePickerDialog(
          activityContext,
          activityContext.getResources().getIdentifier(
            "ClockTimePickerDialog",
            "style",
            activityContext.getPackageName()
          ),
          onTimeSetListener,
          hour,
          minute,
          minuteInterval,
          is24hour,
          display
        );
      } else if (display == RNTimePickerDisplay.SPINNER) {
        return new RNDismissableTimePickerDialog(
          activityContext,
          activityContext.getResources().getIdentifier(
            "SpinnerTimePickerDialog",
            "style",
            activityContext.getPackageName()
          ),
          onTimeSetListener,
          hour,
          minute,
          minuteInterval,
          is24hour,
          display
        );
      }
    }
    return new RNDismissableTimePickerDialog(
      activityContext,
      onTimeSetListener,
      hour,
      minute,
      minuteInterval,
      is24hour,
      display
    );
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(dialog);
    }
  }

  public void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  public void setOnTimeSetListener(@Nullable OnTimeSetListener onTimeSetListener) {
    mOnTimeSetListener = onTimeSetListener;
  }
}
