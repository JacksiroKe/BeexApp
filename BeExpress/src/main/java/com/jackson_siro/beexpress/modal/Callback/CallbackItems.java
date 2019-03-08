package com.jackson_siro.beexpress.modal.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.modal.Items;

public class CallbackItems implements Serializable{
    public int total = -1;
    public List<Items> data = new ArrayList<>();
}
