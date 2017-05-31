package com.dadino.quickstart2.core.adapters

import android.support.v7.widget.RecyclerView
import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : RecyclerView.Adapter<HOLDER>()
