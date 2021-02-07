#!/usr/bin/env bash

#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This should be called from repository root

if [[ "$(./.github-actions/scripts/get_version.sh)" == *-SNAPSHOT ]]; then
  if [[ $1 == release ]]; then
    >&2 echo "Cannot deploy in release mode when version is snapshot"
    exit 1;
  fi
else
  if [[ $1 != release ]]; then
    >&2 echo "Cannot deploy in non-release mode when version is not snapshot"
    exit 1;
  fi;
fi


maven_profiles=build-extras,sign-artifacts,import-env-code-signing-credentials,"$1"-deployment
if [[ $1 == release ]]; then
  maven_profiles="${maven_profiles},automatic-central-release"
fi
echo "Using maven profiles: [${maven_profiles}]"

mvn deploy -s ./.github-actions/maven/sonatype-ossrh-settings.xml --activate-profiles "$maven_profiles" -B -V
