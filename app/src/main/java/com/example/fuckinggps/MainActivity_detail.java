package com.example.fuckinggps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity_detail extends AppCompatActivity {
    private long time;
    private Button detail;
    private Button delete;
    private RecordsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_detail);
         viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        Intent intent =getIntent();
        time= intent.getLongExtra("time", 1);
        setListener();
    }
    private void setListener(){
        detail=findViewById(R.id.detail_buttonId);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_detail.this,MainActivity_location_detail.class);
                intent.putExtra("time",time);
                startActivity(intent);
            }
        });
        delete=findViewById(R.id.delete_buttonId);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_detail.this);
                builder.setTitle("Delete Info");
                builder.setMessage("Are you sure to delete this record?");

                // add the buttons
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.delete(time);
                        Toast.makeText(MainActivity_detail.this,R.string.records_delete,
                                Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

}