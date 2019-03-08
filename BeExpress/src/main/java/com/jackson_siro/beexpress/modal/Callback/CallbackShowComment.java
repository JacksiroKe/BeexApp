package com.jackson_siro.beexpress.modal.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jackson_siro.beexpress.modal.Comment;

public class CallbackShowComment implements Serializable {
    public List<Comment> data = new ArrayList<>();
}
