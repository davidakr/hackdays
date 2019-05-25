package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DialogOut extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogquestion, null);


        builder.setTitle("Ask your Question!")
                .setView(view)
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText edusername = (EditText)view.findViewById(R.id.username);
                        String topic = edusername.getText().toString();

                        Post post = new Post(topic, "lala", "lol", 0);
                        Post.addPost(post);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static DialogOut newInstance(int title) {
        DialogOut frag = new DialogOut();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    void showDialog() {
        DialogFragment newFragment = DialogOut.newInstance(
                2);
        newFragment.show(getFragmentManager(), "dialog");
    }
}
