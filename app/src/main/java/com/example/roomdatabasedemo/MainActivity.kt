package com.example.roomdatabasedemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasedemo.databinding.ActivityMainBinding
import com.example.roomdatabasedemo.databinding.UpdateDialogViewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        if(supportActionBar!=null)supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val employeedao=(application as EmployeeApplication).db.employeeDao()
        binding?.btnAddRecord?.setOnClickListener {
            addRecord(employeedao)
        }
        lifecycleScope.launch{
            employeedao.fetchAllEmployees().collect(){
                val list=ArrayList(it)
                setUpRecordsOfEmployees(list,employeedao)
            }
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        val name=binding?.etName?.text.toString()
        val email=binding?.etEmail?.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name=name, email = email))
                Toast.makeText(applicationContext, "record added successfully", Toast.LENGTH_SHORT).show()
                binding?.etEmail?.text?.clear()
                binding?.etName?.text?.clear()
            }
        }
    }
    fun setUpRecordsOfEmployees(employeeList:ArrayList<EmployeeEntity>,employeeDao: EmployeeDao){
        if(employeeList.isNotEmpty()){
            binding?.tvNorecavailable?.visibility= View.GONE
            binding?.rvList?.visibility=View.VISIBLE
            val myAdapter=ItemAdapter(employeeList,
                {
                    updateId->updateDialogForEntries(updateId,employeeDao)
                },
                {
                    deleteId->deleteDialogForEntries(deleteId,employeeDao)
                }
            )
            binding?.rvList?.layoutManager=LinearLayoutManager(this)
            binding?.rvList?.adapter=myAdapter
        }else{
            binding?.tvNorecavailable?.visibility= View.VISIBLE
            binding?.rvList?.visibility=View.INVISIBLE
        }
    }
    private fun updateDialogForEntries(id:Int,employeeDao: EmployeeDao){
        val updateDialog=Dialog(this,R.style.Theme_dialog)
        val updateBinding=UpdateDialogViewBinding.inflate(layoutInflater)
        updateDialog.setContentView(updateBinding.root)
        updateDialog.setCanceledOnTouchOutside(true)
        lifecycleScope.launch {
            employeeDao.fetchEmployeeByID(id).collect{
                updateBinding.etNameUpdate.setText(it.name)
                updateBinding.etUpdateEmail.setText(it.email)
            }
        }
        updateBinding.btnUpdate.setOnClickListener {
            val name=updateBinding.etNameUpdate.text.toString()
            val email=updateBinding.etUpdateEmail.text.toString()
            if(name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch{
                    employeeDao.update(EmployeeEntity(id,name,email))
                    Toast.makeText(applicationContext, "Data Updated Successfully", Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            }
            else Toast.makeText(applicationContext, "Please enter data in all fields", Toast.LENGTH_SHORT).show()
        }
        updateBinding.btnCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()

    }
    private fun deleteDialogForEntries(id:Int,employeeDao: EmployeeDao){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("DELETE")


        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            lifecycleScope.launch{
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext, "Record Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface, _->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog=builder.create()
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()
    }
}