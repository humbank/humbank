package org.scrobotic.humbank

import org.scrobotic.humbank.data.Account


class AccountRepository(database: Database) {
    private val queries = database.accountsQueries

    fun syncAccounts(accountsFromServer: List<Account>){
        queries.transaction {
            accountsFromServer.forEach { account ->
                queries.insertAccount(
                    username = account.username,
                    full_name = account.full_name,
                    balance = account.balance,
                    role = account.role
                )
            }
        }
    }

    fun getAccount(username: String): Account? =
        queries.selectAccountByUsername(username)
            .executeAsOneOrNull()
            ?.let {
                Account(
                    username = it.username,
                    full_name = it.full_name,
                    balance = it.balance,
                    role = it.role
                )
            }
}
