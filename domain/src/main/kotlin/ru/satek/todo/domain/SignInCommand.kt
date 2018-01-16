package ru.satek.todo.domain

class SignInCommand(private val email: String, private val password: String) : Command<AuthToken> {
    @Throws(WrongEmailOrPassword::class)
    suspend override fun execute(executor: Executor): AuthToken {
        if (email != executor.email || password != executor.password) throw WrongEmailOrPassword()
        return AuthToken(executor.existsUser)
    }
}