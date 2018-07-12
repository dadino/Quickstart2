package com.dadino.quickstart2.core.entities

open class StateCommand

@Deprecated(replaceWith = ReplaceWith("StateCommand"), message = "Replace with StateCommand")
open class ModelCommand : StateCommand()

class IdempotentCommand : StateCommand()
class InitState : StateCommand()
