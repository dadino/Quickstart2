package com.dadino.quickstart2.core.sample.entities

import com.dadino.quickstart2.core.entities.ModelCommand


class SetInProgress : ModelCommand()
class SetIdle : ModelCommand()
class SetError : ModelCommand()
class SetDone(val list: List<ExampleData>) : ModelCommand()
class SetItemSelected(val selectedId: Long?) : ModelCommand()

class SetLoadSessionCompleted(val session: Session) : ModelCommand()
class SetLoadSessionInProgress : ModelCommand()
class SetLoadSessionError(val error: Throwable) : ModelCommand()