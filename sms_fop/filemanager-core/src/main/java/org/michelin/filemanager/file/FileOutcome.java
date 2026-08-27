package org.michelin.filemanager.file;

/**
 * Sealed result of processing one file. Pattern-matchable; no boolean flags.
 *
 *   switch (outcome) {
 *       case Archived a  -> log.info("archived a={} r={}", a.accepted(), a.rejected());
 *       case Rejected r  -> log.warn("rejected: {}", r.reason());
 *       case Skipped s   -> log.info("skipped: {}", s.reason());
 *   }
 */
public sealed interface FileOutcome permits FileOutcome.Archived,
                                            FileOutcome.Rejected,
                                            FileOutcome.Skipped {

    record Archived(int accepted, int rejected) implements FileOutcome {}
    record Rejected(String reason)              implements FileOutcome {}
    record Skipped(SkipReason reason)           implements FileOutcome {}

    enum SkipReason { DUPLICATE_HASH, ALREADY_LOADED }
}
