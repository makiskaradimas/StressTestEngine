Feature: Demo Feature

  Background: Setup common preconditions
    Given Start a new test
    # Settings can be overridden if new values are specified in scenarios
    And The following configuration is used:
      # Used only in cyclic barrier executor
      | Cycle users                | 20000 |
      # Used in both the meta latch and the cyclic barrier
      | Cycles                     | 3    |
      # Warm up cycles are regular cycles that don't count in statistics
      | Warm up cycles             | 2     |
      # If failed tasks exceed the failed threshold the cycles will stop
      | Failed cycles threshold    | 1     |
      # Delay between cycles in milliseconds
      | Delay between cycles       | 20   |
      # Delay between actions in cycle in milliseconds
      | Delay between actions      | 20     |
      # Run async tasks every X milliseconds (you must pause and reschedule the async task to get the new delay)
      | Async task delay           | 1000  |

  # Pause async tasks after the last scenario that you want them running in the background
  Scenario: Async Task
    When Run async tasks in the background continuously
      | Send MO     |
      | Redeem Code |

  Scenario: First scenario (Meta Latch Executor)
    When Pause async tasks
    When Reschedule async tasks
    When Create a stress test containing the following actions:
      | Actions     | Users |
      | Send MO     | 10000 |
      | Redeem Code | 1000  |
  #   | Not existing | 1    |
    When Pause async tasks

  Scenario: Second scenario (Cyclic Barrier Executor)
    When Create a cyclic stress test containing the following actions in this order:
      | Send MO     |
      | Redeem Code |
  #   | Not existing |


  #
  # For the future  (not supported in this version)
  #
  #
#  Scenario: Third scenario
#    # Number of users starts from "Initial cycle users" and increases in each cycle until the cycle reaches a timeout
#    When Create an incremental cyclic stress test containing the following actions in random order:
#      | Send MO       |
#      | Redeem Code   |
#      | Login         |
#      | Submit Answer |
#
#  Scenario: Fourth scenario
#    # The User performs the actions serially with small delay between them without waiting for the previous action to be completed
#    When Create a stress test with "25000" users performing the following actions with specific delay between the start of each action:
#      | Send MO       |
#      | Redeem Code   |
#      | Login         |
#      | Submit Answer |
#
#  Scenario: Fifth scenario
#    # The User performs the actions serially with small delay between the end of the previous action and the start of the next one
#    When Create a stress test with "25000" users performing the following actions with small delay between them:
#      | Send MO       |
#      | Redeem Code   |
#      | Login         |
#      | Submit Answer |