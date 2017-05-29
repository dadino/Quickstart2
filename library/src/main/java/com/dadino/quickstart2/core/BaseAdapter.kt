package com.dadino.quickstart2.core

import android.support.v7.widget.RecyclerView


abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : RecyclerView.Adapter<HOLDER>()
