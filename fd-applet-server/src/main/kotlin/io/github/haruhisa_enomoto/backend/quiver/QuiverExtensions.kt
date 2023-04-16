package io.github.haruhisa_enomoto.backend.quiver

/**
 * Creates a trivial word (of length zero) from a given vertex.
 *
 * @return a trivial word from the vertex.
 */
fun <T, U> T.toTrivialWord(): Word<T, U> {
    return Word.from(emptyList(), this, this, check = false)
}

/**
 * Constructs a monomial from a given list of arrows.
 *
 * @return a monomial created from the list of arrows.
 */
fun <T, U> List<Arrow<T, U>>.toMonomial(): Monomial<T, U> {
    return Monomial(this)
}

/**
 * Constructs a word from a given list of letters.
 *
 * @param check indicates whether to check for the validity of the word.
 * @return a word created from the list of letters.
 */
fun <T, U> List<Letter<T, U>>.toWord(check: Boolean = true): Word<T, U> {
    return Word.from(this, this.first().from, this.last().to, check)
}