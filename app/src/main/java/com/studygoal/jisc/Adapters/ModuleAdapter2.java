package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.R;

import java.util.ArrayList;
import java.util.List;

public class ModuleAdapter2 extends BaseAdapter {
    public ArrayList<String> moduleList;
    public ArrayList<String> coursesList;
    LayoutInflater inflater;
    String selected;

    public ModuleAdapter2(Context context, String selected) {
        this.selected = selected;
        this.moduleList = new ArrayList<>();
        this.coursesList = new ArrayList<>();
        List<Module> moduleList = new Select().from(Module.class).execute();
        List<Courses> coursesList = new Select().from(Courses.class).execute();
        for (int j = 0; j < coursesList.size(); j++) {
            this.coursesList.add(coursesList.get(j).name);
        }
        for (int i = 0; i < moduleList.size(); i++) {
            this.moduleList.add(moduleList.get(i).name);
        }
        this.moduleList.add(0, context.getString(R.string.any_module));
        this.moduleList.addAll(1, this.coursesList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return moduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_layout_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.dialog_item_name);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);
        if (!coursesList.contains(moduleList.get(position)) && position != 0){
            String liniuta = " -" + moduleList.get(position);
            textView.setText(liniuta);
        }else {
            textView.setText(moduleList.get(position));
        }

        if (moduleList.get(position).equals(selected)) {
            convertView.findViewById(R.id.dialog_item_selected).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.dialog_item_selected).setVisibility(View.GONE);
        }
        return convertView;
    }

}
