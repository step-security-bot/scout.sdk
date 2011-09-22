/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.internal.jdt.finegraned;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.BufferChangedEvent;
import org.eclipse.jdt.core.IBufferChangedListener;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.scout.commons.IOUtility;
import org.eclipse.scout.sdk.ScoutSdk;

/**
 * Caches the source of {@link ICompilationUnit}s that are not in sync with their buffer
 */
public class FineGrainedJavaElementDeltaManager {
  private static FineGrainedJavaElementDeltaManager instance = new FineGrainedJavaElementDeltaManager();

  public static FineGrainedJavaElementDeltaManager getInstance() {
    return instance;
  }

  private Object m_cacheLock = new Object();
  private WeakHashMap<IJavaElementDelta, FineGrainedJavaElementDelta[]> m_deltaCache = new WeakHashMap<IJavaElementDelta, FineGrainedJavaElementDelta[]>();
  private HashMap<String/* path */, String/* source */> m_sourceCache = new HashMap<String, String>();
  private IBufferChangedListener m_bufferListener;

  private FineGrainedJavaElementDeltaManager() {
    m_bufferListener = new IBufferChangedListener() {
      @Override
      public void bufferChanged(BufferChangedEvent e) {
        if (e.getBuffer().isClosed()) {
          if (e.getBuffer().getOwner() instanceof ICompilationUnit) {
            ICompilationUnit icu = (ICompilationUnit) e.getBuffer().getOwner();
            synchronized (m_cacheLock) {
              String path = icu.getPath().toString();
              m_sourceCache.remove(path);
            }
          }
        }
      }
    };
  }

  public FineGrainedJavaElementDelta[] getDelta(IJavaElementDelta delta) {
    FineGrainedJavaElementDelta[] astDeltas;
    synchronized (m_cacheLock) {
      astDeltas = m_deltaCache.get(delta);
      if (astDeltas == null) {
        try {
          ICompilationUnit icu = (ICompilationUnit) delta.getElement();
          String path = icu.getPath().toString();
          String oldContent = m_sourceCache.get(path);
          IFile file = (IFile) icu.getResource();
          String bufferContent = null;
          if (file.exists()) bufferContent = IOUtility.getContent(new InputStreamReader(file.getContents(true), file.getCharset()));
          String newContent = icu.getSource();
          // update source cache
          if (oldContent == null) {
            oldContent = bufferContent;
          }
          if (newContent.equals(bufferContent)) {
            m_sourceCache.remove(path);
          }
          else {
            m_sourceCache.put(path, newContent);
            icu.getBuffer().removeBufferChangedListener(m_bufferListener);
            icu.getBuffer().addBufferChangedListener(m_bufferListener);
          }
          //
          astDeltas = new FineGrainedAstAnalyzer(delta).calculateDeltas(oldContent, newContent);
        }
        catch (Exception e) {
          ScoutSdk.logWarning(e);
          astDeltas = new FineGrainedJavaElementDelta[0];
        }
        m_deltaCache.put(delta, astDeltas);
      }
    }
    return astDeltas;
  }

}
