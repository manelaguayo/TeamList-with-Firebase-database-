package com.manelaguayo.todolist;

import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.R.attr.bitmap;


public class NewTodoDialog extends DialogFragment implements View.OnClickListener {

    static int RC_GALLERY_ID= 11111;
    String imageUri;
    String url;
    String name;
    ImageView imageView;
    EditText nameTeam;
    EditText urlTeam;


    public String getUri(){

        return this.imageUri;
    }

    @Override
    public void onClick(View v) {

        if (v.getId()== R.id.AbrirGaleria){

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,RC_GALLERY_ID);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_GALLERY_ID){

            if (data.getData()!=null) {

                Uri tempUri = data.getData();
                imageView.setImageURI(tempUri);
                imageUri = tempUri.toString();
            }
        }
    }

    public interface NewTodoDialogListener {
        void onDialogPositiveClick(NewTodoDialog dialog);
    }

    NewTodoDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view= getActivity().getLayoutInflater().inflate(R.layout.dialog_new_todo, null);
        Button gallery= (Button) view.findViewById(R.id.AbrirGaleria);
        gallery.setOnClickListener(this);

        imageView= (ImageView) view.findViewById(R.id.PreviewFoto);
        nameTeam= (EditText) view.findViewById(R.id.new_todo_title);
        urlTeam= (EditText) view.findViewById(R.id.webTeam);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_new_todo)
                .setView(view)
                .setPositiveButton(R.string.dialog_new_todo_create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        name= nameTeam.getText().toString();
                        url= urlTeam.getText().toString();

                        mListener.onDialogPositiveClick(NewTodoDialog.this);
                    }
                });
        return builder.create();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            mListener = (NewTodoDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewTodoDialogListener");
        }
    }
}
