import { defineMeta } from "blume";

export default defineMeta({
  order: 9,
  title: "ADRs",
  pages: [
    "index",
    "0001-plain-java-no-framework",
    "0002-raw-jdbc-no-orm",
    "0003-batch-jar-no-daemon",
    "0004-pg-cron-over-java-scheduler",
    "0005-outbox-pattern-for-notifications",
    "0006-azure-flexible-postgres",
    "0007-no-inbound-rest",
    "0008-ops-owns-containerization",
    "0009-no-hardcoding",
    "0010-test-driven-development",
    "0011-data-tools-separate-module",
    "0012-files-com-as-file-source",
    "0013-sftp-with-ssh-keys",
    "0014-long-running-with-internal-scheduler",
  ],
});
