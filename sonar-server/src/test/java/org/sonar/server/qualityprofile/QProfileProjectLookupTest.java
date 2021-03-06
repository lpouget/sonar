/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.qualityprofile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.core.component.ComponentDto;
import org.sonar.core.properties.PropertiesDao;
import org.sonar.core.qualityprofile.db.QualityProfileDao;
import org.sonar.core.qualityprofile.db.QualityProfileDto;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QProfileProjectLookupTest {

  @Mock
  QualityProfileDao qualityProfileDao;

  @Mock
  PropertiesDao propertiesDao;

  QProfileProjectLookup lookup;

  @Before
  public void setUp() throws Exception {
    lookup = new QProfileProjectLookup(qualityProfileDao);
  }

  @Test
  public void search_projects() throws Exception {
    QualityProfileDto qualityProfile = new QualityProfileDto().setId(1).setName("My profile").setLanguage("java");
    when(qualityProfileDao.selectProjects("My profile", "sonar.profile.java")).thenReturn(newArrayList(new ComponentDto().setId(1L).setKey("org.codehaus.sonar:sonar").setName("SonarQube")));

    QProfileProjectLookup.QProfileProjects result = lookup.projects(qualityProfile);
    assertThat(result.profile()).isNotNull();
    assertThat(result.projects()).hasSize(1);
  }

  @Test
  public void count_projects() throws Exception {
    lookup.countProjects(new QProfile().setId(1).setName("My profile").setLanguage("java"));
    verify(qualityProfileDao).countProjects("My profile", "sonar.profile.java");
  }

  @Test
  public void search_profiles_from_project() throws Exception {
    QualityProfileDto qualityProfile = new QualityProfileDto().setId(1).setName("My profile").setLanguage("java");
    when(qualityProfileDao.selectByProject(1L, "sonar.profile.%")).thenReturn(newArrayList(qualityProfile));

    List<QProfile> result = lookup.profiles(1L);
    assertThat(result).hasSize(1);
  }

}
