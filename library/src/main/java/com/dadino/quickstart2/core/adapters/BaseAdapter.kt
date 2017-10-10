package com.dadino.quickstart2.core.adapters

import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : android.support.v7.widget.RecyclerView.Adapter<HOLDER>()
