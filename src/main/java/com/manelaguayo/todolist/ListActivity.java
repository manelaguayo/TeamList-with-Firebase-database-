package com.manelaguayo.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends DrawerActivity implements
        DrawerActivity.FabClickListener,
        NewTodoDialog.NewTodoDialogListener {

    MyTodoRecyclerViewAdapter myTodoRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ADD PERMISIONS PROGRAMMATICALLY
        setFabClickListener(this);

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myTodoRecyclerViewAdapter = new MyTodoRecyclerViewAdapter();
        recyclerView.setAdapter(myTodoRecyclerViewAdapter);

        ItemTouchHelper.Callback callback=
                new SimpleItemTouchHelperCallback(myTodoRecyclerViewAdapter);

        ItemTouchHelper touchHelper= new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);


        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("user-teams/"+userId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                TodoItem equipo= dataSnapshot.getValue(TodoItem.class);

                if (!equipo.isBorrat()) {

                    myTodoRecyclerViewAdapter.getList().add(equipo);
                    myTodoRecyclerViewAdapter.notifyDataSetChanged();
                    myTodoRecyclerViewAdapter.notifyItemInserted(myTodoRecyclerViewAdapter.getItemCount());

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                /*TodoItem equipBorrat= dataSnapshot.getValue(TodoItem.class);

                for (int i=0; i<myTodoRecyclerViewAdapter.getList().size(); i++){

                    if (myTodoRecyclerViewAdapter.getList().get(i).getId() == equipBorrat.getId()){

                        myTodoRecyclerViewAdapter.getList().remove(i);
                        myTodoRecyclerViewAdapter.notifyItemRemoved(i);
                    }

                }*/

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onFabClick() {
        new NewTodoDialog().show(getSupportFragmentManager(), "NewTodoDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(NewTodoDialog dialog) {

        writeNewTeam(dialog.name,dialog.url, dialog.imageUri);
        //myTodoRecyclerViewAdapter.getList().add(new TodoItem(dialog.name,dialog.url, dialog.imageUri));
        //myTodoRecyclerViewAdapter.notifyDataSetChanged();

    }

    public void writeNewTeam(String name, String url, String uri){

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        TodoItem equipo= new TodoItem(name, url, uri);

        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference();

        String key= dbref.child("teams").push().getKey();
        equipo.setId(key);
        equipo.setBorrat(false);

        Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/teams/"+key, equipo);
                childUpdates.put("/user-teams/"+userId+"/"+key, equipo);

                dbref.updateChildren(childUpdates);

        }

        //FirebaseDatabase.getInstance().getReference("teams").child(userId).child("teams").setValue(equipo);



    }

class MyTodoRecyclerViewAdapter extends RecyclerView.Adapter<MyTodoRecyclerViewAdapter.CustomViewHolder>
        implements ItemTouchHelperAdapter {
    private List<TodoItem> todoItemList;
    private List<TodoItem> todoItemListRemoved;

    public List<TodoItem> getTodoItemListRemoved() {
        return todoItemListRemoved;
    }

    public MyTodoRecyclerViewAdapter() {
        this.todoItemList = new ArrayList<>();
        this.todoItemListRemoved= new ArrayList<>();
    }

    public List<TodoItem> getList(){
        return todoItemList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        TodoItem todoItem = todoItemList.get(i);

        customViewHolder.textView.setText(todoItem.getTitle());

        if (todoItem.getUri()!=null) {

            Uri uri = Uri.parse(todoItem.getUri());
            customViewHolder.imagen.setImageURI(uri);

        }

        customViewHolder.url.setText(todoItem.getUrl());


    }

    @Override
    public int getItemCount() {
        return (null != todoItemList ? todoItemList.size() : 0);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(todoItemList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

       // todoItemListRemoved.add(todoItemList.get(position));

        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        String id_equipo = todoItemList.get(position).getId();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/teams/"+id_equipo+"/borrat", true);
        childUpdates.put("/user-teams/"+userId+"/"+id_equipo+"/borrat", true);

        db.updateChildren(childUpdates);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected ImageView imagen;
        protected TextView url;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.teamTitle);
            this.imagen = (ImageView) view.findViewById(R.id.logoImage);
            this.url= (TextView) view.findViewById(R.id.urlTeam);
        }
    }
}

interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}

class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }



}
