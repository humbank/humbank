package org.scrobotic.humbank

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.scrobotic.humbank.data.Account
import orgscrobotichumbank.Accounts


class AccountRepository(database: Database) {
    private val queries = database.accountsQueries

    fun syncAccounts(accountsFromServer: List<Account>) {
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


    fun searchAccounts(query: String):
            Flow<List<Accounts>> {
        return queries.searchAccounts(query).asFlow().mapToList(Dispatchers.IO)
    }

}
