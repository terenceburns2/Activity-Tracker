package com.example.activitytracker.record;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.activitytracker.R;

/**
 * Fragment displaying prompt for user to input details about their run before they finish.
 */
public class FinishedDialog extends DialogFragment {

    protected String selectedEffort = "Moderate";

    public interface DialogListener {
        void onDialogPositiveClick(FinishedDialog dialog);
        void onDialogNegativeClick(FinishedDialog dialog);
    }

    DialogListener listener;

    // This dialog obtains a reference to the parent Fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() +
                    " must implement DialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_finish)
               .setSingleChoiceItems(R.array.users_effort, 1, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        selectedEffort = getResources().getStringArray(R.array.users_effort)[which];
                   }
               })
               .setPositiveButton(R.string.finish_activity, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(FinishedDialog.this);
                    }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(FinishedDialog.this);
                    }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
