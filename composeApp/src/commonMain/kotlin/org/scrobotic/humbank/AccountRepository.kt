package org.scrobotic.humbank

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.AllAccount
import orgscrobotichumbank.Accounts
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


class AccountRepository(database: Database) {
    private val queries = database.accountsQueries

    @OptIn(ExperimentalTime::class)
    fun syncAccounts(accountsFromServer: List<AllAccount>) {
        queries.transaction {
            accountsFromServer.forEach { account ->
                queries.insertAccount(
                    username = account.username,
                    role = account.role,
                    full_name = account.full_name,
                    updated_at = account.updated_at.toString()
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getAccount(username: String): AllAccount =
        queries.selectAccountByUsername(username)
            .executeAsOneOrNull()
            ?.let {
                AllAccount(
                    username = it.username,
                    full_name = it.full_name,
                    updated_at = Instant.parse(it.updated_at),
                    role = it.role
                )
            } ?: throw NoSuchElementException("Account not found: $username")


    fun searchAccounts(query: String):
            Flow<List<Accounts>> {
        return queries.searchAccounts(query).asFlow().mapToList(Dispatchers.IO)
    }

    @OptIn(ExperimentalTime::class)
    fun getLatestTime(): String? {
        queries.selectLatestTime().executeAsOneOrNull()?.let {
            return it.MAX
        }
        return null
        }
}
