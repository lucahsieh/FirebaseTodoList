package io.github.lucahsieh.firebaserocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import 	android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextTask;
    EditText editTextWho;
//    EditText editTextDue;
    CheckBox checkboxDone;
    DatePicker picker;
    Button buttonAddToDoItem;

    ListView lvItems;
    List<ToDoItem> itemList;


    DatabaseReference databaseToDoLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseToDoLists= FirebaseDatabase.getInstance().getReference("Items");

        editTextTask=findViewById(R.id.editTextTask);
        editTextWho=findViewById(R.id.editTextWho);
//        editTextDue=findViewById(R.id.editTextDue);
        checkboxDone=findViewById(R.id.checkboxDone);
        buttonAddToDoItem=findViewById(R.id.buttonAddStudent);
        picker=(DatePicker)findViewById(R.id.datePicker1);



        buttonAddToDoItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addItem();
            }
        });

        lvItems = findViewById(R.id.lvItems);
        itemList = new ArrayList<ToDoItem>();

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToDoItem item = itemList.get(position);

                showUpdateDialog(item.getId(),
                        item.getTask(),
                        item.getWho(),
                        item.getDue(),
                        item.isDone());

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseToDoLists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    ToDoItem item = studentSnapshot.getValue(ToDoItem.class);
                    itemList.add(item);
                }

                ItemListAdapter adapter = new ItemListAdapter(MainActivity.this, itemList);
                lvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private void addItem(){
        String task  = editTextTask.getText().toString().trim();
        String who = editTextWho.getText().toString().trim();
//        String dueStr = editTextDue.getText().toString().trim();
//        Date due = null;
        Date due = getDateFromDatePicker(this.picker);
        Boolean done = checkboxDone.isChecked();

        if (TextUtils.isEmpty(task)) {
            Toast.makeText(this, "You must enter a task name.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(who)) {
            Toast.makeText(this, "You must enter a person name.", Toast.LENGTH_LONG).show();
            return;
        }
        if (due == null) {
            Toast.makeText(this, "You must enter a due day.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = databaseToDoLists.push().getKey();
        ToDoItem item = new ToDoItem(id,task,who,due,done);
        Task setValueTask = databaseToDoLists.child(id).setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,"item added", Toast.LENGTH_LONG).show();

                editTextTask.setText("");
                editTextWho.setText("");
                picker.updateDate(2019,11,01);
//                editTextDue.setText("");
                checkboxDone.setChecked(false);

            }
        });
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "fails",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void updateStudent(String id,String task, String who, Date due, boolean done) {
        DatabaseReference dbRef = databaseToDoLists.child(id);

        ToDoItem student = new ToDoItem(id,task,who,due,done);

        Task setValueTask = dbRef.setValue(student);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showUpdateDialog(final String id,String task, String who, Date due, boolean done) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextTask = dialogView.findViewById(R.id.editTextTask);
        editTextTask.setText(task);

        final EditText editTextWho = dialogView.findViewById(R.id.editTextWho);
        editTextWho.setText(who);

        final DatePicker picker = dialogView.findViewById(R.id.datePicker1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(due);


        picker.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE));

        final CheckBox checkBox = dialogView.findViewById(R.id.checkboxDone);
        checkBox.setChecked(done);

        final Button btnUpdate = dialogView.findViewById(R.id.buttonUpdate);

        dialogBuilder.setTitle("Update Item " + task );

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = editTextTask.getText().toString().trim();
                String who = editTextWho.getText().toString().trim();
                Date date = getDateFromDatePicker(picker);
                boolean done = checkboxDone.isChecked();

                if (TextUtils.isEmpty(task)) {
                    editTextTask.setError("First Name is required");
                    return;
                } else if (TextUtils.isEmpty(who)) {
                    editTextWho.setError("Last Name is required");
                    return;
                }

                updateStudent(id,task, who, date, done);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(id);

                alertDialog.dismiss();
            }
        });
    }


    private void deleteItem(String id) {
        DatabaseReference dbRef = databaseToDoLists.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Student Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }








}
