/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */
package org.jamon.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EclipseUtils
{
    public static void delete(IFile p_file) throws CoreException
    {
        if (p_file.exists())
        {
            EclipseUtils.unsetReadOnly(p_file);
            p_file.delete(true, null);
        }
    }

    public static void closeQuiety(InputStream is)
    {
        if (is == null)
        {
            return;
        }
        try
        {
            is.close();
        }
        catch (IOException e)
        {
            // sshh
        }
    }

    public static void logError(Throwable p_error) {
        JamonProjectPlugin.getDefault().logError(p_error);
    }

    public static void logInfo(String p_message) {
        JamonProjectPlugin.getDefault().logInfo(p_message);
    }

    public static CoreException createCoreException(Throwable e)
    {
        return new CoreException(new Status(
            IStatus.ERROR,
            JamonProjectPlugin.getDefault().getBundle().getSymbolicName(),
            0,
            e.getMessage() != null ? e.getMessage() : e.getClass().getName(),
            e));
    }

    public static CoreException createCoreException(String message)
    {
        return new CoreException(new Status(
            IStatus.ERROR,
            JamonProjectPlugin.getDefault().getBundle().getSymbolicName(),
            0,
            message,
            null));
    }

    private EclipseUtils() {}

    public static void populateProblemMarker(
        IMarker p_marker, int p_lineNumber, String p_message, int p_severity)
        throws CoreException
    {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IMarker.LINE_NUMBER, new Integer(p_lineNumber));
        attributes.put(IMarker.MESSAGE, p_message);
        attributes.put(IMarker.SEVERITY, new Integer(p_severity));
        p_marker.setAttributes(attributes);
    }

    public static void unsetReadOnly(IResource p_resource) throws CoreException
    {
        ResourceAttributes resourceAttributes =
            p_resource.getResourceAttributes();
        if (resourceAttributes != null && resourceAttributes.isReadOnly())
        {
            resourceAttributes.setReadOnly(false);
            p_resource.setResourceAttributes(resourceAttributes);
        }
    }
}